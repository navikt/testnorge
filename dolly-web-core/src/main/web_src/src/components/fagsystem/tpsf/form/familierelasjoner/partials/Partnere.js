import React from 'react'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { Alder } from '~/components/fagsystem/tpsf/form/personinformasjon/partials/alder/Alder'
import { Diskresjonskoder } from '~/components/fagsystem/tpsf/form/personinformasjon/partials/diskresjonskoder/Diskresjonskoder'
import Formatters from '~/utils/DataFormatter'

const initialValues = {
	identtype: 'FNR',
	kjonn: '',
	sivilstander: [{ sivilstand: '', sivilstandRegdato: '' }],
	harFellesAdresse: false,
	alder: Formatters.randomIntInRange(18, 99),
	spesreg: '',
	utenFastBopel: false
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
				<Alder basePath={path} formikBag={formikBag} />
				<Diskresjonskoder basePath={path} formikBag={formikBag} />
				<DollyFieldArray
					name={`tpsf.relasjoner.partnere[${idx}].sivilstander`}
					title="Forhold"
					newEntry={initForhold}
				>
					{(path, idx) => (
						<React.Fragment key={idx}>
							<FormikSelect
								name={`${path}.sivilstand`}
								label="Forhold til partner (sivilstand)"
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
