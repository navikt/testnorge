import React from 'react'
import { IdentSearch } from './IdentSearch'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { AdresseKodeverk } from '~/config/kodeverk'
import { FormikCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'
import { FormikProps } from 'formik'

type Props = {
	formikBag: FormikProps<{}>
}

export const IdentNummer = ({ formikBag }: Props) => (
	<section>
		<IdentSearch formikBag={formikBag} />
	</section>
)
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

export const Identitet = () => (
	<section>
		<FormikCheckbox
			name="personinformasjon.identitet.falskIdentitet"
			label="Har falsk identitet"
			size="medium"
		/>
		<FormikCheckbox
			name="personinformasjon.identitet.utenlandskIdentitet"
			label="Har utenlandsk identitet"
			size="medium"
		/>
	</section>
)

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
