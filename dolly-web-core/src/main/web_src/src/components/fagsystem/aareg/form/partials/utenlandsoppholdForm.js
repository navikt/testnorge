import React from 'react'
import _get from 'lodash/get'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FieldArrayRemoveButton } from '~/components/ui/form/fieldArray/DollyFieldArray'
import HjelpeTekst from 'nav-frontend-hjelpetekst'

export const UtenlandsoppholdForm = ({ name, formikBag, fjern }) => {
	const utenlandsoppholdArray = _get(formikBag.values, name, [])
	const harUtenlandsopphold = utenlandsoppholdArray.length > 0

	return (
		<div>
			{harUtenlandsopphold && (
				<div className="flexbox--align-center">
					<h4>Utenlandsopphold</h4>
					<HjelpeTekst>
						Start- og sluttdato må både være innenfor samme kalendermåned i samme år og perioden til
						arbeidsforholdet
					</HjelpeTekst>
				</div>
			)}
			{utenlandsoppholdArray.map((utenlandsopphold, idx) => (
				<div key={idx} className="flexbox">
					<h5 className="nummer">{`#${idx + 1}`}</h5>
					<FormikSelect name={`${name}[${idx}].land`} label="Land" kodeverk="LandkoderISO2" />
					<FormikDatepicker name={`${name}[${idx}].periode.fom`} label="Opphold fra" />
					<FormikDatepicker name={`${name}[${idx}].periode.tom`} label="Opphold til" />
					<FieldArrayRemoveButton onClick={() => fjern(idx, name, utenlandsoppholdArray)} />
				</div>
			))}
		</div>
	)
}
