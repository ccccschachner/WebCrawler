# Web Crawler

## Overview
This project is a simple web crawler built in Java. It allows you to scrape two or more web pages concurrently and extract useful information like headings and links.

## Components
- **EntryPoint**: entry point for starting the program
- **Crawler**: systematically crawls the pages, starting at the given URL
- **CrawlerTask**: is responsible for initialising and executing the crawling of a single URL
- **DomainMatcher**: checks wether the given URL matches the domains entered at the beginning
- **MarkdownCombiner**: combines the seperately created files of the URLs into one report
- **MarkdownContentWriter**: it forms the interface between the crawler and the MarkdownWriter, so that only the MarkdownContentWriter has to be used in the crawler and takes care of all the functionalities of the MarkdownWriter
- **MarkdownFileWriter**: generates output file and interacts directly with the markdown file
- **Parser**: splits html documents and stores extracted elements as string arrays

## Usage
- clone project
- open in IntelliJ IDE
- navigate to src\main\java
- run EntryPoint.main() via run button
- further instructions appear in the terminal

## Test
- navigate to src\test
- right click on java directory > "Run 'tests' in 'java'"

