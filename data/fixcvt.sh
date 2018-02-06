#!/bin/bash
sed  -e 's/&gt;/>/' -e 's/&lt;/</' -e 's/&amp;/&/' -i .bak $1