import React from 'react'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { AdresseKodeverk, PersoninformasjonKodeverk } from '~/config/kodeverk'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'

export const Ident = () => (
	<FormikSelect name="personIdent.type" label="Type" options={Options('identtype')} />
)

export const IdentPaths = {
	'personIdent.type': 'string',
}

export const Nasjonalitet = () => (
	<FormikSelect
		name="statsborger.land"
		label="Statsborgerskap"
		kodeverk={AdresseKodeverk.StatsborgerskapLand}
		optionHeight={50}
	/>
)

export const NasjonalitetPaths = {
	'statsborger.land': 'string',
}

export const Boadresse = () => (
	<>
		<FormikTextInput name="boadresse.adresse" label="Gatenavn" />
		<FormikSelect
			name="boadresse.kommune"
			label="Kommune"
			kodeverk={AdresseKodeverk.Kommunenummer}
		/>
		<FormikSelect
			name="boadresse.postnr"
			label="Postnummer"
			kodeverk={AdresseKodeverk.PostnummerUtenPostboks}
		/>
	</>
)

export const BoadressePaths = {
	'boadresse.adresse': 'string',
	'boadresse.kommune': 'string',
	'boadresse.postnr': 'string',
}

export const Diverse = () => (
	<>
		<FormikSelect
			name="personInfo.kjoenn"
			label="Kjønn"
			kodeverk={PersoninformasjonKodeverk.Kjoennstyper}
		/>
		<FormikSelect
			name="sivilstand.type"
			label="Sivilstand"
			kodeverk={PersoninformasjonKodeverk.Sivilstander}
		/>
	</>
)

export const DiversePaths = {
	'personInfo.kjoenn': 'string',
	'sivilstand.type': 'string',
}
