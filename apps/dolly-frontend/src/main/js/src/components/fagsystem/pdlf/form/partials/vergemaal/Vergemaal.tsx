import React from 'react'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { AvansertForm } from '~/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { initialVergemaal } from '~/components/fagsystem/pdlf/form/initialValues'
import { VergemaalKodeverk } from '~/config/kodeverk'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import _get from 'lodash/get'
import { PdlPersonExpander } from '~/components/fagsystem/pdlf/form/partials/pdlPerson/PdlPersonExpander'
import LoadableComponent from '~/components/ui/loading/LoadableComponent'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import { getPersonOptions } from '~/components/fagsystem/pdlf/form/partials/utils'

export const Vergemaal = ({ formikBag, gruppeId }) => {
	return (
		<div className="flexbox--flex-wrap">
			<FormikDollyFieldArray
				name={'pdldata.person.vergemaal'}
				header="Vergemål"
				newEntry={initialVergemaal}
				canBeEmpty={false}
			>
				{(path: string, idx: number) => {
					return (
						<>
							<FormikSelect
								name={`${path}.vergemaalEmbete`}
								label="Fylkesmannsembete"
								kodeverk={VergemaalKodeverk.Fylkesmannsembeter}
								size="xlarge"
							/>
							<FormikSelect
								name={`${path}.sakType`}
								label="Sakstype"
								kodeverk={VergemaalKodeverk.Sakstype}
								size="xlarge"
								optionHeight={50}
							/>
							<FormikSelect
								name={`${path}.mandatType`}
								label="Mandattype"
								kodeverk={VergemaalKodeverk.Mandattype}
								size="xxlarge"
								optionHeight={50}
							/>
							<FormikDatepicker name={`${path}.gyldigFraOgMed`} label="Gyldig f.o.m." />
							<FormikDatepicker name={`${path}.gyldigTilOgMed`} label="Gyldig t.o.m." />
							<ErrorBoundary>
								<LoadableComponent
									onFetch={() => getPersonOptions(gruppeId)}
									render={(data) => (
										<FormikSelect
											name={`${path}.vergeIdent`}
											label="Verge"
											options={data}
											size={'xlarge'}
										/>
									)}
								/>
							</ErrorBoundary>
							<PdlPersonExpander
								path={`${path}.nyVergeIdent`}
								label={'VERGE'}
								formikBag={formikBag}
								kanSettePersondata={_get(formikBag.values, `${path}.vergeIdent`) === null}
							/>
							<AvansertForm path={path} kanVelgeMaster={false} />
						</>
					)
				}}
			</FormikDollyFieldArray>
		</div>
	)
}
