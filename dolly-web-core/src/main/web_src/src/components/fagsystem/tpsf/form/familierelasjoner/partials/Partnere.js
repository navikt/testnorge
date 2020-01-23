import React from 'react'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { Alder } from '~/components/fagsystem/tpsf/form/personinformasjon/partials/alder/Alder'
import { Diskresjonskoder } from '~/components/fagsystem/tpsf/form/personinformasjon/partials/diskresjonskoder/Diskresjonskoder'
import Formatters from '~/utils/DataFormatter'
import { Sivilstand } from './Sivilstand'

const initialValues = {
	identtype: 'FNR',
	kjonn: '',
	sivilstander: [{ sivilstand: '', sivilstandRegdato: '' }],
	harFellesAdresse: false,
	alder: Formatters.randomIntInRange(18, 99),
	spesreg: '',
	utenFastBopel: false,
	statsborgerskap: '',
	statsborgerskapRegdato: ''
}

export const Partnere = ({ formikBag }) => (
	<FormikDollyFieldArray name="tpsf.relasjoner.partnere" title="Partner" newEntry={initialValues}>
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
				<FormikSelect
					name={`${path}.statsborgerskap`}
					label="Statsborgerskap"
					kodeverk="Landkoder"
				/>
				<FormikDatepicker name={`${path}.statsborgerskapRegdato`} label="Statsborgerskap fra" />
				<Diskresjonskoder basePath={path} formikBag={formikBag} />
				<Alder basePath={path} formikBag={formikBag} title="Alder" />
				<Sivilstand basePath={`tpsf.relasjoner.partnere[${idx}].sivilstander`} />
			</React.Fragment>
		)}
	</FormikDollyFieldArray>
)
