import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import React from 'react'

export const YtelseForm = ({ path, header, initialYtelser }: any) => {
	return (
		<div className={'fullWidth tp-form'}>
			<FormDollyFieldArray name={path} header={header} newEntry={initialYtelser} nested>
				{(path: any, idx: React.Key) => (
					<React.Fragment key={idx}>
						<FormSelect
							name={`${path}.type`}
							label="Ytelse"
							size="large"
							isClearable={false}
							options={Options('tjenestepensjonYtelseType')}
						/>
						<FormDatepicker name={`${path}.datoInnmeldtYtelseFom`} label="Medlemskap f.o.m." />
						<FormDatepicker name={`${path}.datoYtelseIverksattFom`} label="Ytelse f.o.m." />
						<FormDatepicker name={`${path}.datoYtelseIverksattTom`} label="Ytelse t.o.m." />
					</React.Fragment>
				)}
			</FormDollyFieldArray>
		</div>
	)
}
