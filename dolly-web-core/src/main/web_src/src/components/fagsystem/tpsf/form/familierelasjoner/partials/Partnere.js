import React from 'react'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'

const initialValues = {
	identtype: 'FNR',
	kjonn: '',
	sivilstander: [initForhold],
	harFellesAdresse: true
}
const initForhold = { sivilstand: '', sivilstandRegdato: '' }

export const Partnere = ({ formikBag }) => (
	<DollyFieldArray name="tpsf.relasjoner.partnere" title="Partner" newEntry={initialValues}>
		{(path, idx) => (
			<React.Fragment key={idx}>
				<FormikSelect
					name={`${path}.identtype`}
					label="Identtype"
					options={Options('identtype')}
					isClearable={false}
				/>
				<FormikSelect name={`${path}.kjonn`} label="Kjønn" kodeverk="Kjønnstyper" />
				<FormikCheckbox name={`${path}.harFellesAdresse`} label="Har felles adresse" />
				<DollyFieldArray
					name={`tpsf.relasjoner.partnere[${idx}].sivilstander`}
					title="Forhold"
					newEntry={initForhold}
				>
					{(path, idx) => (
						<React.Fragment key={idx}>
							<FormikSelect name={`${path}.sivilstand`} label="Forhold" kodeverk="Sivilstander" />
							<FormikDatepicker name={`${path}.sivilstandRegdato`} label="Forhold fra dato" />
						</React.Fragment>
					)}
				</DollyFieldArray>
			</React.Fragment>
		)}
	</DollyFieldArray>
)
