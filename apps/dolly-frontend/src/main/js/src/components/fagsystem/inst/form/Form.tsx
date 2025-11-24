import React from 'react'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { erForsteEllerTest, panelError } from '@/components/ui/form/formUtils'
import Panel from '@/components/ui/panel/Panel'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { validation } from '@/components/fagsystem/inst/form/validation'
import {
	BestillingsveilederContextType,
	useBestillingsveileder,
} from '@/components/bestillingsveileder/BestillingsveilederContext'
import { getExcludedDatesAndMaxDate } from './utils'
import { addYears } from 'date-fns'
import { useFormContext } from 'react-hook-form'

export const initialValues = {
	institusjonstype: '',
	startdato: '',
	forventetSluttdato: '',
	sluttdato: '',
}
export const instAttributt = 'instdata'

export const InstForm = () => {
	const formMethods = useFormContext()
	const opts = useBestillingsveileder() as BestillingsveilederContextType
	const { personFoerLeggTil }: any = opts

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
				hasErrors={panelError(instAttributt)}
				iconType="institusjon"
				startOpen={erForsteEllerTest(formMethods.getValues(), [instAttributt])}
			>
				{/*// @ts-ignore*/}
				<FormDollyFieldArray name="instdata" header="Opphold" newEntry={initialValues}>
					{(path, idx) => (
						<React.Fragment key={idx}>
							<FormSelect
								name={`${path}.institusjonstype`}
								label="Institusjonstype"
								options={Options('institusjonstype')}
								isClearable={false}
							/>
							<FormDatepicker
								name={`${path}.startdato`}
								label="Startdato"
								excludeDates={excludeDates}
								maxDate={maxDate}
							/>
							<FormDatepicker
								name={`${path}.forventetSluttdato`}
								label="Forventet sluttdato"
								excludeDates={excludeDates}
								maxDate={maxDate}
							/>
							<FormDatepicker
								name={`${path}.sluttdato`}
								label="Sluttdato"
								excludeDates={excludeDates}
								maxDate={maxDate}
							/>
						</React.Fragment>
					)}
				</FormDollyFieldArray>
			</Panel>
		</Vis>
	)
}

InstForm.validation = validation
