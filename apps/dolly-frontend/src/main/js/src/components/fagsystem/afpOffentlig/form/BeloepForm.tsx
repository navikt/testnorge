import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { initialBeloep } from '@/components/fagsystem/afpOffentlig/initialValues'
import React from 'react'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'

export const BelopForm = ({ path }: { path: string }) => {
	return (
		<FormDollyFieldArray name={path} header="Beløp" newEntry={initialBeloep} nested>
			{(belop: string, idx: number) => (
				<div key={idx} className="flexbox">
					<FormDatepicker name={`${belop}.fomDato`} label="F.o.m. dato" />
					<FormTextInput name={`${belop}.belop`} label="Beløp" type="number" />
				</div>
			)}
		</FormDollyFieldArray>
	)
}
