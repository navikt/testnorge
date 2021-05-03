import React from 'react'
import _get from 'lodash/get'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { SelectOptionsOppslag } from '~/service/SelectOptionsOppslag'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { TimeloennetForm } from './timeloennetForm'
import { PermisjonForm } from './permisjonForm'
import { PermitteringForm } from './permitteringForm'
import { UtenlandsoppholdForm } from './utenlandsoppholdForm'
import { ArbeidsavtaleForm } from './arbeidsavtaleForm'
import { MaritimtArbeidsforholdForm } from './maritimtArbeidsforholdForm'
import { OrgnummerToggle } from './orgnummerToggle'
import { ArbeidKodeverk } from '~/config/kodeverk'
import Hjelpetekst from '~/components/hjelpetekst'

export const ArbeidsforholdForm = ({ path, formikBag, erLenket, brukerId }) => {
	const arbeidsforhold = _get(formikBag.values, path)

	console.log('erLenket 2:>> ', erLenket)

	const arbeidsforholdIndex = path.charAt(path.length - 1)
	const arbeidsforholdstype = _get(formikBag.values, 'aareg[0].arbeidsforholdstype')

	const virksomheter = SelectOptionsOppslag.hentVirksomheterFraOrgforvalter(brukerId)
	const virksomheterOptions = SelectOptionsOppslag.formatOptions('virksomheter', virksomheter)

	const onChangeLenket = (field, fieldPath) => {
		const amelding = _get(formikBag.values, 'aareg[0].amelding')
		console.log('erLenket 3:>> ', erLenket)
		if (erLenket) {
			amelding.forEach((maaned, idx) => {
				formikBag.setFieldValue(
					`aareg[0].amelding[${idx}].arbeidsforhold[${arbeidsforholdIndex}].${fieldPath}`,
					field.value
				)
			})
		} else {
			formikBag.setFieldValue(`${path}.${fieldPath}`, field.value)
		}
	}

	return (
		<React.Fragment>
			<div className="flexbox--flex-wrap">
				<FormikSelect
					name={`${path}.arbeidsgiver.orgnummer`}
					label="Organisasjonsnummer"
					//TODO: Laster ikke med en gang
					options={virksomheterOptions}
					size="xxlarge"
					isClearable={false}
				/>
				{/* //TODO arbeidsforholdId blir ikke oppdatert */}
				<FormikTextInput name={`${path}.arbeidsforholdId`} label="Arbeidsforhold-ID" type="text" />
				<FormikDatepicker name={`${path}.ansettelsesPeriode.fom`} label="Ansatt fra" />
				<FormikDatepicker name={`${path}.ansettelsesPeriode.tom`} label="Ansatt til" />
				<FormikSelect
					name={`${path}.ansettelsesPeriode.sluttaarsak`}
					label="Sluttårsak"
					kodeverk={ArbeidKodeverk.SluttaarsakAareg}
					size="xlarge"
					onChange={field => onChangeLenket(field, 'ansettelsesPeriode.sluttaarsak')}
					disabled={
						_get(formikBag.values, `${path}.ansettelsesPeriode.tom`) === null ? true : false
					}
					// TODO disabled funker ikke!
				/>
			</div>

			<ArbeidsavtaleForm formikBag={formikBag} path={path} />
			{arbeidsforholdstype === 'maritimtArbeidsforhold' && (
				<MaritimtArbeidsforholdForm formikBag={formikBag} path={`${path}.fartoy`} />
			)}

			<TimeloennetForm path={`${path}.antallTimerForTimeloennet`} />

			<UtenlandsoppholdForm path={`${path}.utenlandsopphold`} />

			<PermisjonForm path={`${path}.permisjon`} />

			<PermitteringForm path={`${path}.permittering`} />
		</React.Fragment>
	)
}
