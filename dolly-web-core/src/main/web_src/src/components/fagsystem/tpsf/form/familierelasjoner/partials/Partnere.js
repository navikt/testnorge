import React from 'react'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'

const initialValues = {
	identtype: 'FNR',
	kjonn: '',
	sivilstander: [{ sivilstand: '', sivilstandRegdato: '' }],
	harFellesAdresse: false
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
							<FormikSelect
								name={`${path}.sivilstand`}
								label="Sivilstand"
								kodeverk="Sivilstander"
								isClearable={false}
							/>
							<FormikDatepicker
								name={`${path}.sivilstandRegdato`}
								label="Sivilstand fra dato"
								isClearable={false}
							/>
						</React.Fragment>
					)}
				</DollyFieldArray>
			</React.Fragment>
		)}
	</DollyFieldArray>
)
