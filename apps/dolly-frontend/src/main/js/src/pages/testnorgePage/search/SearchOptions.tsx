import React from 'react'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { AdresseKodeverk } from '~/config/kodeverk'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FormikCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'

export default () => (
	<>
		<h2>Personinformasjon</h2>
		<section>
			<h3>Fødsels- eller d-nummer</h3>
			<FormikTextInput
				name="personinformasjon.ident.ident"
				label="Fødsels- eller d-nummer"
				visHvisAvhuket={false}
				maxlength="11"
			/>
		</section>
		<section>
			<h3>Alder</h3>
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
			<FormikCheckbox name="personinformasjon.barn.barn" label="Har barn" checkboxMargin />
			<FormikCheckbox
				name="personinformasjon.barn.doedfoedtBarn"
				label="Har dødfødt barn"
				checkboxMargin
			/>
		</section>
		<section>
			<h3>Identitet</h3>
			<FormikCheckbox
				name="personinformasjon.identitet.falskIdentitet"
				label="Har falsk identitet"
				checkboxMargin
			/>
			<FormikCheckbox
				name="personinformasjon.identitet.utenlandskIdentitet"
				label="Har utenlandsk identitet"
				checkboxMargin
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
		</section>
	</>
)
