import React, { useState } from 'react'
import _get from 'lodash'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'

export const Adressat = ({ formikBag }) => {
	//TODO HER MANGLER CONDITIONALS ETTERSOM HVILKEN ADRESSATTYPE
	// INKLUDERT FUNKSJONALITET FOR Å HÅNDTERE ENDRING AV ADRESSATTYPE UNDERVEIS
	return (
		<Kategori title="Adressat">
			<FormikSelect
				name="kontaktinformasjonForDoedsbo.adressat.adressatType"
				label="Adressattype"
				options={Options('adressatType')}
			/>

			<FormikTextInput name="kontaktinformasjonForDoedsbo.adressat.idnummer" label="Fnr/dnr/bost" />

			<FormikDatepicker
				name="kontaktinformasjonForDoedsbo.adressat.foedselsdato"
				label="Fødselsdato"
			/>
			<FormikTextInput name="kontaktinformasjonForDoedsbo.adressat.fornavn" label="Fornavn" />
			<FormikTextInput name="kontaktinformasjonForDoedsbo.adressat.mellomnavn" label="Mellomnavn" />
			<FormikTextInput name="kontaktinformasjonForDoedsbo.adressat.etternavn" label="Etternavn" />
			<FormikTextInput
				name="kontaktinformasjonForDoedsbo.adressat.organisajonsnavn"
				label="Organisasjonsnavn"
			/>
			<FormikTextInput
				name="kontaktinformasjonForDoedsbo.adressat.organisajonsnummer"
				label="Organisasjonsnummer"
			/>
		</Kategori>
	)
}
