import React, { useContext } from 'react'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { erForsteEllerTest, panelError } from '@/components/ui/form/formUtils'
import Panel from '@/components/ui/panel/Panel'
import { FormikDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { FormikDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { validation } from '@/components/fagsystem/inst/form/validation'
import { BestillingsveilederContext } from '@/components/bestillingsveileder/BestillingsveilederContext'
import { getExcludedDatesAndMaxDate } from './utils'
import { addYears } from 'date-fns'

export const initialValues = {
	institusjonstype: '',
	startdato: '',
	sluttdato: '',
}
export const instAttributt = 'instdata'

export const InstForm = ({ formMethods }) => {
	const opts = useContext(BestillingsveilederContext)
	const { personFoerLeggTil } = opts

	let excludeDates = []
	let maxDate = addYears(new Date(), 5)
	if (opts.is.leggTil && personFoerLeggTil.instdata !== undefined) {
		const dateInfo = getExcludedDatesAndMaxDate(personFoerLeggTil)
		excludeDates = dateInfo[0]
		maxDate = dateInfo[1]
	}

	return (
		<Vis attributt={instAttributt}>
			<Panel
				heading="Institusjonsopphold"
				hasErrors={panelError(formMethods.formState.errors, instAttributt)}
				iconType="institusjon"
				startOpen={erForsteEllerTest(formMethods.getValues(), [instAttributt])}
			>
				{/*// @ts-ignore*/}
				<FormikDollyFieldArray name="instdata" header="Opphold" newEntry={initialValues}>
					{(path, idx) => (
						<React.Fragment key={idx}>
							<FormikSelect
								name={`${path}.institusjonstype`}
								label="Institusjonstype"
								options={Options('institusjonstype')}
								isClearable={false}
							/>
							<FormikDatepicker
								name={`${path}.startdato`}
								label="Startdato"
								excludeDates={excludeDates}
								maxDate={maxDate}
							/>
							<FormikDatepicker
								name={`${path}.sluttdato`}
								label="Sluttdato"
								excludeDates={excludeDates}
								maxDate={maxDate}
							/>
						</React.Fragment>
					)}
				</FormikDollyFieldArray>
			</Panel>
		</Vis>
	)
}

InstForm.validation = validation
