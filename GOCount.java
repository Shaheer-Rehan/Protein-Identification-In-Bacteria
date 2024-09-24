import java.io.IOException; 
import org.apache.hadoop.conf.Configuration; 
import org.apache.hadoop.fs.Path; 
import org.apache.hadoop.io.IntWritable; 
import org.apache.hadoop.io.Text; 
import org.apache.hadoop.mapreduce.Job; 
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Mapper.Context; 
import org.apache.hadoop.mapreduce.Reducer; 
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat; 
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

/** 
This class counts the number of times the GO:0030420 term or one of its children appear in the input files. 
It matches the appearance of the term to the file name (which represents the organism) and the DB Object ID (which
represents the Protein ID or Gene ID).  
*/
public class GOCount 
{ 
    /**
    This class maps the input files to the GO terms and the DB Object IDs. The map method in this class takes
    the input file and splits it into fields. It then checks if the GO term in each line is GO:0030420 or one of its
    children. If it is, it outputs the file name, DB Object ID, and GO term as the key and the value is 1.
    */
    public static class GOCountMapper 
        extends Mapper<Object, Text, Text, IntWritable> 
        {
            private final static IntWritable one = new IntWritable(1); 
            private Text outputKey = new Text(); 
        
            public void map(Object key, Text value, Context context) throws IOException, InterruptedException 
            { 
                FileSplit fileSplit = (FileSplit)context.getInputSplit();
                String filename = fileSplit.getPath().getName();
            
                String[] fields = value.toString().split("\t");
                if (fields.length >= 3) 
                {
                    String DBObjectID = fields[1]; 
                    String GOTerm = fields[4]; 
            
                    if (GOTerm.equals("GO:0030420") || 
                        GOTerm.equals("GO:0045809") || 
                        GOTerm.equals("GO:0045304") || 
                        GOTerm.equals("GO:0045808")) 
                    {
                        outputKey.set(filename + "\t" + DBObjectID + "\t" + GOTerm);
                        context.write(outputKey, one); 
                    }
                }
            } 
        } 
    
    /**
    This class reduces the output of the mapper class. It takes a key and its corresponding values and sums the values.
    It then outputs the key and the sum of the values.
    */
    public static class IntSumReducer 
        extends Reducer<Text, IntWritable, Text, IntWritable> 
        { 
            private IntWritable result = new IntWritable(); 
        
            public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException 
            { 
                int sum = 0; 
                for (IntWritable val : values) 
                { 
                    sum += val.get(); 
                } 
                result.set(sum); 
                context.write(key, result); 
            } 
        } 
    
    /**
    This method sets up and runs a MapReduce job. It takes the input files directory and the output directory as arguments.
    The job uses the GOCountMapper class as the mapper, the IntSumReducer class as both the combiner and the reducer.
    The output keys are of type Text, and the output values are of type IntWritable.
    The job is then run and the method waits for its completion before exiting.
    */
    public static void main(String[] args) throws Exception 
        { 
            Configuration conf = new Configuration(); 
            Job job = Job.getInstance(conf, "GO Counter"); 
            job.setJarByClass(GOCount.class); 
            job.setMapperClass(GOCountMapper.class); 
            job.setCombinerClass(IntSumReducer.class); 
            job.setReducerClass(IntSumReducer.class); 
            job.setOutputKeyClass(Text.class); 
            job.setOutputValueClass(IntWritable.class); 
            FileInputFormat.addInputPath(job, new Path(args[0])); 
            FileOutputFormat.setOutputPath(job, new Path(args[1])); 
            System.exit(job.waitForCompletion(true) ? 0 : 1); 
        } 
}