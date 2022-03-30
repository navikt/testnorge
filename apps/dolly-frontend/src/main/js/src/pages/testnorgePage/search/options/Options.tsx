import React from 'react'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { AdresseKodeverk } from '~/config/kodeverk'
import { FormikCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'

export const Alder = () => (
	<section>
		<FormikTextInput
			name="personinformasjon.alder.fra"
			label="Alder fra"
			visHvisAvhuket={false}
			size="medium"
		/>
		<FormikTextInput
			name="personinformasjon.alder.til"
			label="Alder til"
			visHvisAvhuket={false}
			size="medium"
		/>
		<FormikDatepicker
			name="personinformasjon.alder.foedselsdato.fom"
			label="Fødselsdato fom"
			visHvisAvhuket={false}
			size="medium"
		/>
		<FormikDatepicker
			name="personinformasjon.alder.foedselsdato.tom"
			label="Fødselsdato tom"
			visHvisAvhuket={false}
			size="medium"
		/>
	</section>
)

export const AlderPaths = {
	'personinformasjon.alder.fra': 'string',
	'personinformasjon.alder.til': 'string',
	'personinformasjon.alder.foedselsdato.fom': 'string',
	'personinformasjon.alder.foedselsdato.tom': 'string',
}

export const Statsborgerskap = () => (
	<section>
		<FormikSelect
			name="personinformasjon.statsborgerskap.land"
			label="Statsborgerskap"
			kodeverk={AdresseKodeverk.StatsborgerskapLand}
			optionHeight={50}
			size="medium"
		/>
	</section>
)

export const StatsborgerskapPaths = {
	'personinformasjon.statsborgerskap.land': 'string',
}

export const Sivilstand = () => (
	<section>
		<FormikSelect
			name="personinformasjon.sivilstand.type"
			label="Sivilstand"
			options={[
				{ value: 'GIFT', label: 'Gift' },
				{ value: 'UGIFT', label: 'Ugift' },
			]}
			size="medium"
		/>
	</section>
)

export const SivilstandPaths = {
	'personinformasjon.sivilstand.type': 'string',
}

export const Barn = () => (
	<section>
		<FormikCheckbox name="personinformasjon.barn.barn" label="Har barn" size="medium" />
		<FormikCheckbox
			name="personinformasjon.barn.doedfoedtBarn"
			label="Har dødfødt barn"
			size="medium"
		/>
	</section>
)

export const BarnPaths = {
	'personinformasjon.barn.barn': 'boolean',
	'personinformasjon.barn.doedfoedtBarn': 'boolean',
}

export const Adresse = () => (
	<section>
		<FormikSelect
			name="personinformasjon.adresse.postnr"
			label="Postnummer"
			kodeverk={AdresseKodeverk.Postnummer}
			optionHeight={50}
			size="medium"
		/>
		<FormikSelect
			name="personinformasjon.adresse.kommunenr"
			label="Kommunenummer"
			kodeverk={AdresseKodeverk.Kommunenummer}
			optionHeight={50}
			size="medium"
		/>
	</section>
)

export const AdressePaths = {
	'personinformasjon.adresse.postnr': 'string',
	'personinformasjon.adresse.kommunenr': 'string',
}

export const Diverse = () => (
	<section>
		<FormikSelect
			name="personinformasjon.diverse.kjoenn"
			label="Kjønn"
			options={[
				{ value: 'KVINNE', label: 'Kvinne' },
				{ value: 'MANN', label: 'Mann' },
			]}
			size="medium"
		/>
		<FormikCheckbox
			name="personinformasjon.diverse.innflyttet"
			label="Har innflyttet til Norge"
			size="medium"
		/>
		<FormikCheckbox
			name="personinformasjon.diverse.utflyttet"
			label="Har utflyttet fra Norge"
			size="medium"
		/>
	</section>
)

export const DiversePaths = {
	'personinformasjon.diverse.kjoenn': 'string',
	'personinformasjon.diverse.innflyttet': 'boolean',
	'personinformasjon.diverse.utflyttet': 'boolean',
}
