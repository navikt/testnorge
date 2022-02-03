import * as React from 'react'
import { BaseSyntheticEvent, useEffect, useState } from 'react'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { initialBarn, initialForelder } from '~/components/fagsystem/pdlf/form/initialValues'
import { FormikProps } from 'formik'
import _get from 'lodash/get'
import { PdlPersonExpander } from '~/components/fagsystem/pdlf/form/partials/pdlPerson/PdlPersonExpander'
import { AvansertForm } from '~/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { FormikCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'
import { ToggleGruppe, ToggleKnapp } from '~/components/ui/toggle/Toggle'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import { SelectOptionsOppslag } from '~/service/SelectOptionsOppslag'

interface ForelderForm {
	formikBag: FormikProps<{}>
	personFoerLeggTil: object
	gruppeId: string
}

const RELASJON_BARN = 'Barn'
const RELASJON_FORELDER = 'Forelder'

export const ForelderBarnRelasjon = ({ formikBag, personFoerLeggTil, gruppeId }: ForelderForm) => {
	const [identOptions, setIdentOptions] = useState([])
	useEffect(() => {
		SelectOptionsOppslag.hentGruppeIdentOptions(gruppeId).then((response: []) =>
			setIdentOptions(response)
		)
	}, [])
	return (
		<FormikDollyFieldArray
			name="pdldata.person.forelderBarnRelasjon"
			header={'Relasjon'}
			newEntry={initialBarn}
			canBeEmpty={true}
		>
			{(path: string, idx: number) => {
				const erBarn = _get(formikBag.values, path)?.partnerErIkkeForelder !== undefined
				return (
					<div className="flexbox--flex-wrap">
						<div className="toggle--wrapper">
							<ToggleGruppe
								onChange={(event: BaseSyntheticEvent) => {
									const toggleBarn = event.target.value
									formikBag.setFieldValue(
										path,
										toggleBarn === RELASJON_BARN ? initialBarn : initialForelder
									)
								}}
								name={'toggler' + idx}
							>
								<ToggleKnapp value={RELASJON_BARN} checked={erBarn}>
									{RELASJON_BARN.toUpperCase()}
								</ToggleKnapp>
								<ToggleKnapp value={RELASJON_FORELDER} checked={!erBarn}>
									{RELASJON_FORELDER.toUpperCase()}
								</ToggleKnapp>
							</ToggleGruppe>
						</div>

						<ErrorBoundary>
							<FormikSelect
								name={`${path}.relatertPerson`}
								label={erBarn ? RELASJON_BARN : RELASJON_FORELDER}
								options={identOptions}
								size={'xlarge'}
							/>
						</ErrorBoundary>

						{(erBarn && (
							<FormikCheckbox
								name={`${path}.partnerErIkkeForelder`}
								label="Partner ikke forelder"
								checkboxMargin
							/>
						)) || (
							<FormikSelect
								name={`${path}.relatertPersonsRolle`}
								label="ForeldreType"
								options={Options('foreldreTypePDL')}
								isClearable={false}
							/>
						)}
						<FormikCheckbox name={`${path}.borIkkeSammen`} label="Bor ikke sammen" checkboxMargin />
						<PdlPersonExpander
							path={`${path}.nyRelatertPerson`}
							label={erBarn ? RELASJON_BARN.toUpperCase() : RELASJON_FORELDER.toUpperCase()}
							formikBag={formikBag}
							kanSettePersondata={_get(formikBag.values, `${path}.relatertPerson`) === null}
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
