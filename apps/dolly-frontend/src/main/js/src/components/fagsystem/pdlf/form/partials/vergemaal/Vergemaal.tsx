import React from 'react'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { AvansertForm } from '~/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { initialVergemaal } from '~/components/fagsystem/pdlf/form/initialValues'
import { VergemaalKodeverk } from '~/config/kodeverk'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import _get from 'lodash/get'
import { PdlPersonExpander } from '~/components/fagsystem/pdlf/form/partials/pdlPerson/PdlPersonExpander'
import { FormikProps } from 'formik'
import { isEmpty } from '~/components/fagsystem/pdlf/form/partials/utils'

interface VergemaalForm {
	formikBag: FormikProps<{}>
}

export const Vergemaal = ({ formikBag }: VergemaalForm) => {
	return (
		<div className="flexbox--flex-wrap">
			<FormikDollyFieldArray
				name={'pdldata.person.vergemaal'}
				header="Vergemål"
				newEntry={initialVergemaal}
				canBeEmpty={false}
			>
				{(path: string, _idx: number) => {
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
							<PdlPersonExpander
								nyPersonPath={`${path}.nyVergeIdent`}
								eksisterendePersonPath={`${path}.vergeIdent`}
								label={'VERGE'}
								formikBag={formikBag}
								isExpanded={
									!isEmpty(_get(formikBag.values, `${path}.nyVergeIdent`)) ||
									_get(formikBag.values, `${path}.vergeIdent`) !== null
								}
							/>
							<AvansertForm path={path} kanVelgeMaster={false} />
						</>
					)
				}}
			</FormikDollyFieldArray>
		</div>
	)
}
