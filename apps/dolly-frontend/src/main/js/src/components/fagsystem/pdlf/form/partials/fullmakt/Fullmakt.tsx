import * as React from 'react'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { Option, SelectOptionsOppslag } from '~/service/SelectOptionsOppslag'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { AvansertForm } from '~/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { PdlPersonExpander } from '~/components/fagsystem/pdlf/form/partials/pdlPerson/PdlPersonExpander'
import { initialFullmakt } from '~/components/fagsystem/pdlf/form/initialValues'
import { FormikProps } from 'formik'
import Loading from '~/components/ui/loading/Loading'

interface FullmaktForm {
	formikBag: FormikProps<{}>
	identOptions: Array<Option>
	loadingOptions: boolean
}

export const Fullmakt = ({ formikBag, identOptions, loadingOptions }: FullmaktForm) => {
	const fullmaktOmraader = SelectOptionsOppslag.hentFullmaktOmraader()
	const fullmaktOptions = SelectOptionsOppslag.formatOptions('fullmaktOmraader', fullmaktOmraader)

	return (
		<FormikDollyFieldArray
			name="pdldata.person.fullmakt"
			header="Fullmakt"
			newEntry={initialFullmakt}
			canBeEmpty={false}
		>
			{(path: string) => {
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
						{loadingOptions && <Loading label="Henter valg for eksisterende ident..." />}
						{identOptions?.length > 0 && (
							<FormikSelect
								name={`${path}.motpartsPersonident`}
								label="Fullmektig"
								options={identOptions}
								size={'xlarge'}
							/>
						)}
						<PdlPersonExpander
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
