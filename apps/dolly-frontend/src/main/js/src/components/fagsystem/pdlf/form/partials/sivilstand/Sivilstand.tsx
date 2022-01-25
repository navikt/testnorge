import * as React from 'react'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { PdlPersonExpander } from '~/components/fagsystem/pdlf/form/partials/pdlPerson/PdlPersonExpander'
import { AvansertForm } from '~/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { initialSivilstand } from '~/components/fagsystem/pdlf/form/initialValues'
import { FormikProps } from 'formik'
import { FormikCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'
import _get from 'lodash/get'
import { DollyApi, PdlforvalterApi } from '~/service/Api'
import LoadableComponent from '~/components/ui/loading/LoadableComponent'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import { Person, PersonData } from '~/components/fagsystem/pdlf/PdlTypes'

interface SivilstandForm {
	formikBag: FormikProps<{}>
	gruppeId: string
}

type Option = {
	value: string
	label: string
}

export const Sivilstand = ({ formikBag, gruppeId }: SivilstandForm) => {
	const getOptions = async () => {
		const gruppe = await DollyApi.getGruppeById(gruppeId).then((response: any) => {
			return response.data?.identer?.map((person: PersonData) => {
				if (person.master === 'PDL' || person.master === 'PDLF') return person.ident
			})
		})
		const options = await PdlforvalterApi.getPersoner(gruppe).then((response: any) => {
			const personListe: Array<Option> = []
			response.data.forEach((id: Person) => {
				personListe.push({
					value: id.person.ident,
					label: `${id.person.ident} - ${id.person.navn[0].fornavn} ${id.person.navn[0].etternavn}`,
				})
			})
			return personListe
		})
		return options ? options : Promise.resolve()
	}

	return (
		<FormikDollyFieldArray
			name="pdldata.person.sivilstand"
			header="Sivilstand"
			newEntry={initialSivilstand}
			canBeEmpty={false}
		>
			{(path: string) => {
				return (
					<div className="flexbox--flex-wrap">
						<FormikSelect
							name={`${path}.type`}
							label="Type sivilstand"
							options={Options('sivilstandType')}
							isClearable={false}
						/>
						<FormikDatepicker
							name={`${path}.sivilstandsdato`}
							label="Gyldig fra og med"
							disabled={_get(formikBag.values, `${path}.bekreftelsesdato`) != null}
							fastfield={false}
						/>
						<FormikDatepicker
							name={`${path}.bekreftelsesdato`}
							label="Bekreftelsesdato"
							disabled={
								_get(formikBag.values, `${path}.sivilstandsdato`) != null ||
								_get(formikBag.values, `${path}.master`) !== 'PDL'
							}
							fastfield={false}
						/>
						<FormikCheckbox name={`${path}.borIkkeSammen`} label="Bor ikke sammen" checkboxMargin />
						<ErrorBoundary>
							<LoadableComponent
								onFetch={() => getOptions()}
								render={(data) => (
									<FormikSelect
										name={`${path}.relatertVedSivilstand`}
										label="Person relatert til"
										options={data}
										size={'xlarge'}
									/>
								)}
							/>
						</ErrorBoundary>
						<PdlPersonExpander
							path={`${path}.nyRelatertPerson`}
							label={'PERSON RELATERT TIL'}
							formikBag={formikBag}
							kanSettePersondata={_get(formikBag.values, `${path}.relatertVedSivilstand`) === null}
						/>
						<AvansertForm
							path={path}
							kanVelgeMaster={_get(formikBag.values, `${path}.bekreftelsesdato`) === null}
						/>
					</div>
				)
			}}
		</FormikDollyFieldArray>
	)
}
