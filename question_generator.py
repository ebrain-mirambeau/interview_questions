'''Using the question template (question_template), generate all possible combinations
of questions by replacing the placeholders (e.g. "<Company>") in the question
with the corresponding fillers (e.g. "Apple"). Print each possible combination
on a separate line.

Example output:

"What was Apple's revenue in 2014?"
...
"What was Oracle's income in 2017?"

There should be one sentence for each possible combination of fillers.
'''

'''Input data:
question_template: a string
filler_dict: a dictionary
'''
question_template = "What was <Company>'s <Metric> in <Year>?"
filler_dict = {"Company": ["Apple", "Microsoft", "Oracle"], "Year": ["2014", "2015", "2016", "2017"], "Metric": ["revenue", "income"]}

'''The combine() method should fill the placeholders in a question template with
the corresponding filler values, generating a unique sentence for each possible
variation and adding it to the list of generated questions. Please complete
this method.
'''

import copy

#The purpose of the following set of functions is to:
# 1. Create a cartesian product of values
# 2. Iterate through them
# 3. Replace tagged data with corresponding values in cartesian product


def cartesian_product(dictionary):
    #calcules cartesian product of values in dictionary
    #stores values in lists to be iterated over
    keys = dictionary.keys()
    values = []

    for key in keys:
        temp = []
        for value in dictionary[key]:
            temp.append((key, value))
        values.append(temp)

    return combinations(values)

def combinations(listOfLists):
    #takes a list of lists and generates combinations of words to
    #be searched for in template

    i = False;
    j = 1;

    cartesian = []
    l = [] #used in first iteration of cross product calculation
    a = len(listOfLists)

    while(j < a):

        if i == False:
            #take first two elements of lists of list and compute cartesian product and build on that
            l = combination_helper(listOfLists[j-1], listOfLists[j])
            cartesian.append(l)
            i = True
        else:
            temp = combination_helper(cartesian[0], listOfLists[j])
            cartesian = []
            cartesian.append(temp)

        j += 1

    return (cartesian[0])
    

def combination_helper(list1, list2):
    #takes two lists and list tuples consisting of elements in each of the lists
    new_list = []
    for element1 in list1:
        for element2 in list2:
            if type(element1) != list:
                new_list.append([element1, element2])
            else:
                temp = copy.deepcopy(element1)
                temp.append(element2)
                new_list.append(temp)

    return new_list
    
def combine(template, fillers):

    variants = []

    cp = cartesian_product(fillers)

    for tpl in cp:
        temp = template
        for element in tpl:
            tag = element[0]
            replacement = element[1]
            temp = temp.replace("<"+element[0]+">", element[1])
        variants.append(temp)
	
    return variants

'''The main() method calls the combine() method to generate the list of
questions and prints each question variant in the list.
'''
def main():
    variants = combine(question_template, filler_dict)
    for v in variants:
        print(v)
  
if __name__ == "__main__":
    main()
