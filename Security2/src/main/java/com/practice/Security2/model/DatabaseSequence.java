package com.practice.Security2.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "database_sequences")
public class DatabaseSequence {
    @Id
    private String id;  // Sequence name
    private long seq;   // Sequence number
}
