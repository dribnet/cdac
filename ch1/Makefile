# this makefile downloads all of the web files into the
# data directory
#
# you can also "make fresh" to download new copies if the
# server copies have changed

data_dir ?= data
url_dir ?= http://www.ericrochester.com/clj-data-analysis/data

web_files = small-sample.csv small-sample-header.csv small-sample.json small-sample-header.xls small-sample.sqlite small-sample.xml

initial_downloads = $(patsubst %,$(data_dir)/%.initial,$(web_files))
freshen_downloads = $(patsubst %,$(data_dir)/%.freshen,$(web_files))
downloads = $(patsubst %,$(data_dir)/%,$(web_files))

default: $(downloads)
fresh: $(freshen_downloads)

$(data_dir):
	@mkdir -p $@

$(downloads): $(data_dir) $(initial_downloads)

$(data_dir)/%.initial:
	curl -o $(data_dir)/$* $(url_dir)/$*
	touch $@

$(data_dir)/%.freshen: $(downloads)
	curl -o $(data_dir)/$* -z $(data_dir)/$* $(url_dir)/$*
