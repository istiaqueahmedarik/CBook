package com.codebook;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ContributionStruct {
    public String username;
    public String userImage;
    public int contribution;
    public String id_token;

}
