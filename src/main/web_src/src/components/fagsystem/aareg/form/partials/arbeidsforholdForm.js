import React from 'react'
import _get from 'lodash/get'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { TimeloennetForm } from './timeloennetForm'
import { PermisjonForm } from './permisjonForm'
import { UtenlandsoppholdForm } from './utenlandsoppholdForm'
import { ArbeidsavtaleForm } from './arbeidsavtaleForm'
import { OrgnummerToggle } from './orgnummerToggle'
import { ArbeidKodeverk } from '~/config/kodeverk'
import { ArbeidsgiverIdent } from '~/components/fagsystem/aareg/form/partials/arbeidsgiverIdent.tsx'

export const ArbeidsforholdForm = ({ path, formikBag }) => {
	const arbeidsforhold = _get(formikBag.values, path)

	const clearOrgnrIdent = aktoer => {
		formikBag.setFieldValue(`${path}.arbeidsgiver.aktoertype`, aktoer.value)
		formikBag.setFieldValue(`${path}.arbeidsgiver.orgnummer`, '')
		formikBag.setFieldValue(`${path}.arbeidsgiver.ident`, '')
	}

	return (
		<React.Fragment>
			<div className="flexbox--flex-wrap">
				<FormikDatepicker name={`${path}.ansettelsesPeriode.fom`} label="Ansatt fra" />
				<FormikDatepicker name={`${path}.ansettelsesPeriode.tom`} label="Ansatt til" />
				<FormikSelect
					name={`${path}.arbeidsforholdstype`}
					label="Type arbeidsforhold"
					kodeverk={ArbeidKodeverk.Arbeidsforholdstyper}
					size="large"
					isClearable={false}
				/>
				<FormikSelect
					name={`${path}.arbeidsgiver.aktoertype`}
					label="Type arbeidsgiver"
					options={Options('aktoertype')}
					onChange={clearOrgnrIdent}
					size="medium"
					isClearable={false}
				/>
				{arbeidsforhold.arbeidsgiver.aktoertype === 'PERS' && (
					<ArbeidsgiverIdent formikBag={formikBag} path={`${path}.arbeidsgiver.ident`} />
				)}
			</div>
			{arbeidsforhold.arbeidsgiver.aktoertype === 'ORG' && (
				<OrgnummerToggle formikBag={formikBag} path={`${path}.arbeidsgiver.orgnummer`} />
			)}

			<ArbeidsavtaleForm formikBag={formikBag} path={path} />

			<TimeloennetForm path={`${path}.antallTimerForTimeloennet`} />

			<PermisjonForm path={`${path}.permisjon`} />

			<UtenlandsoppholdForm path={`${path}.utenlandsopphold`} />
		</React.Fragment>
	)
}
