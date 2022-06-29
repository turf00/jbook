# Working Effectivey with Legacy Code by Michael Feathers

## Chapter 1: Changing Software

4 reasons to change software:

1. Add a feature
2. Fix a bug
3. Improve design
4. Optimise resource usage

### Adding Features & Fixing Bugs

* Some orgs will argue as to whether an issue falls into bugs or features, which I have plenty of experience on my recent jobs.
* The book argues that it is more important to identify whether the change is:
  + To add new behaviour
  + To change old behaviour

> Behaviour is the most important thing about software. It is what users depend on. Users like it when we add behaviour... but if we change or remove behaviour they depend on, then they stop trusting us.

Example with A1 and the change of the hostname calculation.

He argues that adding behaviour will inevitably change behaviour as well.

### Improving design

Behaviour = the same, how it is achieved will change.
In order to prove the behaviour is the same:
  + Tests to prove behaviour
  + Make small change

### Optimisation

Functionality = same, performance = improved

### Summary

