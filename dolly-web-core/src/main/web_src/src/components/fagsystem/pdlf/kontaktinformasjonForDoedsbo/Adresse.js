import React, { useState } from 'react'
import _get from 'lodash'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'

export const Adresse = ({ formikBag }) => {
	//TODO HER MANGLER STYRING PÅ POSTNR OG POSTSTED ETTERSOM HVILKET LAND SOM ER VALGT
	return (
		<Kategori title="Adresse">
			<FormikSelect
				name="kontaktinformasjonForDoedsbo.adressat.landkode"
				label="Land"
				kodeverk="Landkoder"
			/>
			<FormikTextInput
				name="kontaktinformasjonForDoedsbo.adressat.adresselinje1"
				label="Adresselinje 1"
			/>
			<FormikTextInput
				name="kontaktinformasjonForDoedsbo.adressat.adresselinje2"
				label="Adresselinje 2"
			/>
			<FormikSelect
				name="kontaktinformasjonForDoedsbo.adressat.postnummer"
				label="Postnummer" //' og -sted'
				kodeverk="Postnummer" // Må sette .poststed utifra kodeverk for land=Norge
			/>
			<FormikSelect
				name="kontaktinformasjonForDoedsbo.adressat.poststed"
				label="Poststed"
				kodeverk="Postnummer" // Settes manuelt for land != Norge
			/>
		</Kategori>
	)
}
