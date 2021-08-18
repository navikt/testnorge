import * as React from 'react'
// @ts-ignore
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { AdresseKodeverk, PersoninformasjonKodeverk } from '~/config/kodeverk'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { antallResultatOptions } from '~/pages/soekMiniNorge/search/utils'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'

export const SearchOptions = () => (
	<>
		<FormikSelect
			name="antallResultat"
			label="Maks antall resultat"
			options={antallResultatOptions}
		/>
		<h2>Personinformasjon</h2>
		<h3>Ident</h3>
		<FormikSelect name="personIdent.type" label="Type" options={Options('identtype')} />
		<h3>Nasjonalitet</h3>
		<FormikSelect
			name="statsborger.land"
			label="Statsborgerskap"
			kodeverk={AdresseKodeverk.StatsborgerskapLand}
			optionHeight={50}
		/>
		<h3>Boadresse</h3>
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
		<h3>Diverse</h3>
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
