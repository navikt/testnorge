import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { FormikDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { FormikDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import React from 'react'

export const YtelseForm = ({ path, header, initialYtelser }: any) => {
	return (
		<div className={'fullWidth tp-form'}>
			<FormikDollyFieldArray name={path} header={header} newEntry={initialYtelser} nested>
				{(path: any, idx: React.Key) => (
					<React.Fragment key={idx}>
						<FormikSelect
							name={`${path}.type`}
							label="Ytelse"
							size="xsmall"
							isClearable={false}
							options={Options('tjenestepensjonYtelseType')}
						/>
						<FormikDatepicker name={`${path}.datoInnmeldtYtelseFom`} label="Medlemskap f.o.m." />
						<FormikDatepicker name={`${path}.datoYtelseIverksattFom`} label="Ytelse f.o.m." />
						<FormikDatepicker name={`${path}.datoYtelseIverksattTom`} label="Ytelse t.o.m." />
					</React.Fragment>
				)}
			</FormikDollyFieldArray>
		</div>
	)
}
