from SPARQLWrapper import SPARQLWrapper, XML , N3 , JSON , JSONLD
from os import listdir
from os.path import isfile, join

def main():
    queryFilesFolder = "q"
    queryFileSuffix = ".query"
    resultFileSuffix = ".ttl"
    DBPEDIA_SPARQL_ENDPOINT = "http://dbpedia.org/sparql"
    
    sparql = SPARQLWrapper(DBPEDIA_SPARQL_ENDPOINT)
    queryFilesList = [f for f in listdir(queryFilesFolder) if isfile(join(queryFilesFolder, f)) and f.endswith(queryFileSuffix)]
    for f in queryFilesList:
        qFilePath = queryFilesFolder + "/" + f
        print("Reading query from " + qFilePath)
        with open(qFilePath, 'r') as qTextFile:
            queryText=qTextFile.read().replace('\n', ' ')
            sparql.setQuery(queryText)
            sparql.setReturnFormat(N3)
            print("Executing query from " + qFilePath)
            results = str(sparql.query().convert())
            results = results[2:-1].replace("\\t","\t").replace("\\n","\n")
            resultsFileName = qFilePath.replace(queryFileSuffix,resultFileSuffix)
            print("Writing results to " + resultsFileName)
            fOut = open(resultsFileName, "w")
            fOut.write(results)
            fOut.flush()
            fOut.close()  
    print("Done.")
if __name__=="__main__":
    main();