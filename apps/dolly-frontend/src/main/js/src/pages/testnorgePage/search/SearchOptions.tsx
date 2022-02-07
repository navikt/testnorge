import React from 'react'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { AdresseKodeverk } from '~/config/kodeverk'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FormikCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'
import { IdentSearch } from '~/pages/testnorgePage/search/IdentSearch'
import { FormikProps } from 'formik'

type SearchOptionsProps = {
	formikBag: FormikProps<{}>
}

export const SearchOptions = (props: SearchOptionsProps) => (
	<>
		<h2>Personinformasjon</h2>
		<section>
			<h3>Fødsels- eller D-nummer</h3>
			<IdentSearch formikBag={props.formikBag} />
		</section>
		<section>
			<h3>Alder</h3>
			<FormikTextInput
				name="personinformasjon.alder.fra"
				label="Alder fra"
				visHvisAvhuket={false}
			/>
			<FormikTextInput
				name="personinformasjon.alder.til"
				label="Alder til"
				visHvisAvhuket={false}
			/>
			<FormikDatepicker
				name="personinformasjon.alder.foedselsdato.fom"
				label="Fødselsdato fom"
				visHvisAvhuket={false}
			/>
			<FormikDatepicker
				name="personinformasjon.alder.foedselsdato.tom"
				label="Fødselsdato tom"
				visHvisAvhuket={false}
			/>
		</section>
		<section>
			<h3>Statsborgerskap</h3>
			<FormikSelect
				name="personinformasjon.statsborgerskap.land"
				label="Statsborgerskap"
				kodeverk={AdresseKodeverk.StatsborgerskapLand}
				optionHeight={50}
			/>
		</section>
		<section>
			<h3>Sivilstand</h3>
			<FormikSelect
				name="personinformasjon.sivilstand.type"
				label="Sivilstand"
				options={[
					{ value: 'GIFT', label: 'Gift' },
					{ value: 'UGIFT', label: 'Ugift' },
				]}
			/>
		</section>
		<section>
			<h3>Barn</h3>
			<FormikCheckbox name="personinformasjon.barn.barn" label="Har barn" />
			<FormikCheckbox name="personinformasjon.barn.doedfoedtBarn" label="Har dødfødt barn" />
		</section>
		<section>
			<h3>Identitet</h3>
			<FormikCheckbox
				name="personinformasjon.identitet.falskIdentitet"
				label="Har falsk identitet"
			/>
			<FormikCheckbox
				name="personinformasjon.identitet.utenlandskIdentitet"
				label="Har utenlandsk identitet"
			/>
		</section>
		<section>
			<h3>Diverse</h3>
			<FormikSelect
				name="personinformasjon.diverse.kjoenn"
				label="Kjønn"
				options={[
					{ value: 'KVINNE', label: 'Kvinne' },
					{ value: 'MANN', label: 'Mann' },
				]}
			/>
			<FormikCheckbox
				name="personinformasjon.diverse.innflyttet"
				label="Har innflyttet til Norge"
			/>
			<FormikCheckbox name="personinformasjon.diverse.utflyttet" label="Har utflyttet fra Norge" />
		</section>
	</>
)
