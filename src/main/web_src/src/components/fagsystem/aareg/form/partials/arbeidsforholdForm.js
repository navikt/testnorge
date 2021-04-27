import React from 'react'
import _get from 'lodash/get'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { TimeloennetForm } from './timeloennetForm'
import { PermisjonForm } from './permisjonForm'
import { UtenlandsoppholdForm } from './utenlandsoppholdForm'
import { ArbeidsavtaleForm } from './arbeidsavtaleForm'
import { OrgnummerToggle } from './orgnummerToggle'
import { ArbeidKodeverk } from '~/config/kodeverk'
import Hjelpetekst from '~/components/hjelpetekst'

export const ArbeidsforholdForm = ({ path, formikBag }) => {
	const arbeidsforhold = _get(formikBag.values, path)
	// console.log('arbeidsforhold :>> ', arbeidsforhold)
	console.log('path :>> ', path)

	// const clearOrgnrIdent = aktoer => {
	// 	formikBag.setFieldValue(`${path}.arbeidsgiver.aktoertype`, aktoer.value)
	// 	formikBag.setFieldValue(`${path}.arbeidsgiver.orgnummer`, '')
	// 	formikBag.setFieldValue(`${path}.arbeidsgiver.ident`, '')
	// }

	// console.log('path :>> ', path)
	// console.log(
	// 	'_get(formikBag.values:>> ',
	// 	_get(formikBag.values, 'aareg[0].ansettelsesPeriode.tom')
	// )

	console.log(
		'get(formikBag.values, `${path}.ansettelsesPeriode.tom`)  :>> ',
		_get(formikBag.values, `${path}.ansettelsesPeriode.tom`)
	)

	return (
		<React.Fragment>
			<div className="flexbox--flex-wrap">
				{/* //TODO hent egne orgnr */}
				<FormikSelect
					name={`${path}.arbeidsgiver.orgnummer`}
					label="Organisasjonsnummer"
					size="xxlarge"
					isClearable={false}
				/>
				<FormikTextInput name={`${path}.arbeidsforholdId`} label="Arbeidsforhold-ID" type="text" />
				<FormikDatepicker name={`${path}.ansettelsesPeriode.fom`} label="Ansatt fra" />
				<FormikDatepicker name={`${path}.ansettelsesPeriode.tom`} label="Ansatt til" />
				<FormikSelect
					name={`${path}.ansettelsesPeriode.sluttaarsak`}
					label="Sluttårsak"
					kodeverk={ArbeidKodeverk.SluttaarsakAareg}
					size="xlarge"
					disabled={
						_get(formikBag.values, `${path}.ansettelsesPeriode.tom`) === null ? true : false
					}
					// TODO disabled funker ikke!
				/>
				{/* <FormikSelect
					name={`${path}.arbeidsforholdstype`}
					label="Type arbeidsforhold"
					kodeverk={ArbeidKodeverk.Arbeidsforholdstyper}
					size="large"
					isClearable={false}
				/> */}
				{/* <FormikSelect
					name={`${path}.arbeidsgiver.aktoertype`}
					label="Type arbeidsgiver"
					options={Options('aktoertype')}
					onChange={clearOrgnrIdent}
					size="medium"
					isClearable={false}
				/> */}
				{/* {arbeidsforhold.arbeidsgiver.aktoertype === 'PERS' && (
					<FormikTextInput name={`${path}.arbeidsgiver.ident`} label="Arbeidsgiver ident" />
				)} */}
			</div>
			{/* {arbeidsforhold.arbeidsgiver.aktoertype === 'ORG' && (
				<OrgnummerToggle formikBag={formikBag} path={`${path}.arbeidsgiver.orgnummer`} />
			)} */}

			<ArbeidsavtaleForm formikBag={formikBag} path={path} />

			{/* <TimeloennetForm path={`${path}.antallTimerForTimeloennet`} />

			<PermisjonForm path={`${path}.permisjon`} />

			<UtenlandsoppholdForm path={`${path}.utenlandsopphold`} /> */}
		</React.Fragment>
	)
}
