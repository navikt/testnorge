import * as React from 'react'
// @ts-ignore
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FormikProps } from 'formik'
import NavButton from '~/components/ui/button/NavButton/NavButton'
import { AdresseKodeverk, PersoninformasjonKodeverk } from '~/config/kodeverk'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { antallResultatOptions } from '~/pages/soekMiniNorge/search/utils'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'

interface SearchOptionsProps {
	formikBag: FormikProps<{}>
	onSubmit: (x:unknown) => void
}

export const SearchOptions = (props: SearchOptionsProps) => {
	return (
		<>
			<div className="search-field__options-container__fields">
				<FormikSelect
					name="antallResultat"
					label="Maks antall resultat"
					options={antallResultatOptions}
				/>
				<h2>Personinformasjon</h2>
				<h3>Ident</h3>
				<FormikSelect
					name="personIdent.type"
					label="Type"
					options={Options('identtype')}
				/>
				<h3>Navn</h3>
				<FormikTextInput name="navn.fornavn" label="Fornavn" />
				<FormikTextInput name="navn.mellomnavn" label="Mellomnavn" />
				<FormikTextInput name="navn.slektsnavn" label="Etternavn" />
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
					kodeverk={AdresseKodeverk.Postnummer}
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
				<FormikDatepicker name="personInfo.datoFoedt" label="Dato født" />
			</div>
			<div className="search-button">
				<NavButton onClick={() => props.onSubmit(props.formikBag.values)}>Søk</NavButton>
			</div>
		</>
	)
}