import * as React from 'react'
import { FormikDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { initialDoedfoedtBarn } from '@/components/fagsystem/pdlf/form/initialValues'
import { FormikProps } from 'formik'
import * as _ from 'lodash-es'
import { AvansertForm } from '@/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { FormikDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { DatepickerWrapper } from '@/components/ui/form/inputs/datepicker/DatepickerStyled'

interface DoedfoedtBarnProps {
	formikBag: FormikProps<{}>
	path?: string
}

export const DoedfoedtBarnForm = ({ formMethods, path }: DoedfoedtBarnProps) => {
	return (
		<div className="flexbox--flex-wrap">
			<DatepickerWrapper>
				<FormikDatepicker
					name={`${path}.dato`}
					label="Dødsdato"
					fastfield={false}
					maxDate={new Date()}
				/>
			</DatepickerWrapper>
			<AvansertForm
				path={path}
				kanVelgeMaster={_.get(formMethods.getValues(), `${path}.bekreftelsesdato`) === null}
			/>
		</div>
	)
}

export const DoedfoedtBarn = ({ formMethods }: DoedfoedtBarnProps) => {
	return (
		<FormikDollyFieldArray
			name="pdldata.person.doedfoedtBarn"
			header={'Dødfødt barn'}
			newEntry={initialDoedfoedtBarn}
			canBeEmpty={false}
		>
			{(path: string) => <DoedfoedtBarnForm formMethods={formMethods} path={path} />}
		</FormikDollyFieldArray>
	)
}
