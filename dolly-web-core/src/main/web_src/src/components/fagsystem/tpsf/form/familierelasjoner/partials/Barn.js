import React from 'react'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FormikCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'

const initialValues = {
	identtype: '',
	kjonn: '',
	barnType: '',
	partnerNr: '',
	borHos: '',
	erAdoptert: false
}

export const Barn = ({ formikBag }) => (
	<DollyFieldArray name="tpsf.relasjoner.barn" title="Barn" newEntry={initialValues}>
		{(path, idx) => (
			<React.Fragment key={idx}>
				<FormikSelect name={`${path}.identtype`} label="Identtype" options={Options('identtype')} />
				<FormikSelect name={`${path}.kjonn`} label="Kjønn" options={Options('kjonnBarn')} />
				<FormikSelect name={`${path}.barnType`} label="Forelder" options={Options('barnType')} />
				<FormikTextInput name={`${path}.partnerNr`} label="Forelder - partner nr." type="number" />
				<FormikSelect name={`${path}.borHos`} label="Bor hos" options={Options('barnBorHos')} />
				<FormikCheckbox name={`${path}.erAdoptert`} label="Er adoptert" />
			</React.Fragment>
		)}
	</DollyFieldArray>
)
