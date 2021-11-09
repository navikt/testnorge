import * as React from 'react'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { SelectOptionsOppslag } from '~/service/SelectOptionsOppslag'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { FormikCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'
import { AvansertForm } from '~/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { PdlPersonForm } from '~/components/fagsystem/pdlf/form/partials/pdlPerson/PdlPersonForm'

const initialFullmakt = {
	omraader: [],
	gyldigFraOgMed: null,
	gyldigTilOgMed: null,
	nyFullmektig: {
		identtype: null,
		nyttNavn: {
			harMellomnavn: false,
		},
	},
	// motpartsPersonident???: null,
	kilde: 'Dolly',
	master: 'PDL',
	gjeldende: true,
}

export const Fullmakt = ({ formikBag }) => {
	const fullmaktOmraader = SelectOptionsOppslag.hentFullmaktOmraader()
	const fullmaktOptions = SelectOptionsOppslag.formatOptions('fullmaktOmraader', fullmaktOmraader)
	console.log('formikBag', formikBag)
	return (
		<FormikDollyFieldArray
			name="pdldata.person.fullmakt"
			header="Fullmakt"
			newEntry={initialFullmakt}
			canBeEmpty={false}
		>
			{(path, idx) => {
				return (
					<div className="flexbox--flex-wrap">
						<div className="flexbox--full-width">
							<FormikSelect
								name={`${path}.omraader`}
								label="Områder"
								options={fullmaktOptions}
								size="grow"
								isMulti={true}
								isClearable={false}
								fastfield={false}
							/>
						</div>
						<FormikDatepicker name={`${path}.gyldigFraOgMed`} label="Gyldig fra og med" />
						<FormikDatepicker name={`${path}.gyldigTilOgMed`} label="Gyldig til og med" />
						<PdlPersonForm
							path={`${path}.nyFullmektig`}
							label={'FULLMEKTIG'}
							formikBag={formikBag}
						/>
						<AvansertForm path={path} kanVelgeMaster={false} />
					</div>
				)
			}}
		</FormikDollyFieldArray>
	)
}
