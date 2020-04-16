import React, { useContext } from 'react'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import { panelError } from '~/components/ui/form/formUtils'
import { erForste } from '~/components/ui/form/formUtils'
import Panel from '~/components/ui/panel/Panel'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { validation } from '~/components/fagsystem/inst/form/validation'
import { BestillingsveilederContext } from '~/components/bestillingsveileder/Bestillingsveileder'
import { getAllDatesBetween } from './utils'
import { addYears } from 'date-fns'
import _isNil from 'lodash/isNil'

const initialValues = {
	institusjonstype: '',
	startdato: '',
	faktiskSluttdato: ''
}
const instAttributt = 'instdata'

export const InstForm = ({ formikBag }) => {
	const opts = useContext(BestillingsveilederContext)
	const { data } = opts

	let excludeDates = []
	let maxDate = null
	if (opts.is.leggTil && data.instdata !== undefined) {
		for (let i = 0; i < data.instdata.length; i++) {
			const startdato = data.instdata[i].startdato
			const sluttdato = data.instdata[i].faktiskSluttdato
			let days = []

			if (_isNil(sluttdato)) {
				const start = new Date(startdato)
				maxDate = start.setDate(start.getDate() - 1)
			} else{
				days = getAllDatesBetween(new Date(startdato), new Date(sluttdato))
				excludeDates = excludeDates.concat(days)
			}
		}
	}

	maxDate = maxDate === null ? addYears(new Date(), 5) : maxDate

	return (
		<Vis attributt={instAttributt}>
			<Panel
				heading="Institusjonsopphold"
				hasErrors={panelError(formikBag, instAttributt)}
				iconType="institusjon"
				startOpen={() => erForste(formikBag.values, [instAttributt])}
			>
				<FormikDollyFieldArray name="instdata" header="Opphold" newEntry={initialValues}>
					{(path, idx) => (
						<React.Fragment key={idx}>
							<FormikSelect
								name={`${path}.institusjonstype`}
								label="Institusjonstype"
								options={Options('institusjonstype')}
								isClearable={false}
							/>
							<FormikDatepicker name={`${path}.startdato`} label="Startdato" excludeDates={excludeDates}
																maxDate={maxDate}/>
							<FormikDatepicker name={`${path}.faktiskSluttdato`} label="Sluttdato" excludeDates={excludeDates}
																maxDate={maxDate}/>
						</React.Fragment>
					)}
				</FormikDollyFieldArray>
			</Panel>
		</Vis>
	)
}

InstForm.validation = validation