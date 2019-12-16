import React from 'react'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'

const initialValues = {
	identtype: '',
	kjonn: '',
	sivilstander: { sivilstand: '', sivilstandRegdato: '' }, // Array på denne??
	harFellesAdresse: false
}

export const Partnere = ({ formikBag }) => (
	<DollyFieldArray name="tpsf.relasjoner.partnere" title="Partner" newEntry={initialValues}>
		{(path, idx) => (
			<React.Fragment key={idx}>
				<FormikSelect name={`${path}.identtype`} label="Identtype" options={Options('identtype')} />
				<FormikSelect name={`${path}.kjonn`} label="Kjønn" kodeverk="Kjønnstyper" />
				<FormikSelect
					name={`${path}.sivilstander.sivilstand`}
					label="Forhold"
					kodeverk="Sivilstander"
				/>
				<FormikDatepicker
					name={`${path}.sivilstander.sivilstandRegdato`}
					label="Forhold fra dato"
				/>
				<FormikCheckbox name={`${path}.harFellesAdresse`} label="Har felles adresse" />
			</React.Fragment>
		)}
	</DollyFieldArray>
)
