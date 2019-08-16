java -jar C:/data/adoc/swagger2markup-cli-1.3.3.jar convert -i ../swagger_models/hcop-otn.yaml -f ./hcop-otn -c ./config.properties
asciidoctor -a toc=left -a toc-title=目录 -a toclevels=3 ./hcop-otn.adoc
