import { FormikDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { AvansertForm } from '@/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { initialVergemaal } from '@/components/fagsystem/pdlf/form/initialValues'
import { VergemaalKodeverk } from '@/config/kodeverk'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { FormikDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import * as _ from 'lodash'
import { PdlPersonExpander } from '@/components/fagsystem/pdlf/form/partials/pdlPerson/PdlPersonExpander'
import { isEmpty } from '@/components/fagsystem/pdlf/form/partials/utils'
import { DatepickerWrapper } from '@/components/ui/form/inputs/datepicker/DatepickerStyled'
import { Option } from '@/service/SelectOptionsOppslag'
import { UseFormReturn } from 'react-hook-form/dist/types'

interface VergemaalFormTypes {
	formMethods: UseFormReturn
	path?: string
	eksisterendeNyPerson?: Option
}

export const VergemaalForm = ({
	formMethods,
	path,
	eksisterendeNyPerson = null,
}: VergemaalFormTypes) => {
	return (
		<>
			<FormikSelect
				name={`${path}.vergemaalEmbete`}
				label="Fylkesmannsembete"
				kodeverk={VergemaalKodeverk.Fylkesmannsembeter}
				size="large"
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
			<DatepickerWrapper>
				<FormikDatepicker name={`${path}.gyldigFraOgMed`} label="Gyldig f.o.m." />
				<FormikDatepicker name={`${path}.gyldigTilOgMed`} label="Gyldig t.o.m." />
			</DatepickerWrapper>
			<PdlPersonExpander
				nyPersonPath={`${path}.nyVergeIdent`}
				eksisterendePersonPath={`${path}.vergeIdent`}
				eksisterendeNyPerson={eksisterendeNyPerson}
				label={'VERGE'}
				formMethods={formMethods}
				isExpanded={
					!isEmpty(_.get(formMethods.getValues(), `${path}.nyVergeIdent`), ['syntetisk']) ||
					_.get(formMethods.getValues(), `${path}.vergeIdent`) !== null
				}
			/>
			<AvansertForm path={path} kanVelgeMaster={false} />
		</>
	)
}

export const Vergemaal = ({ formMethods }: VergemaalFormTypes) => {
	return (
		<div className="flexbox--flex-wrap">
			<FormikDollyFieldArray
				name={'pdldata.person.vergemaal'}
				header="Vergemål"
				newEntry={initialVergemaal}
				canBeEmpty={false}
			>
				{(path: string, _idx: number) => <VergemaalForm formMethods={formMethods} path={path} />}
			</FormikDollyFieldArray>
		</div>
	)
}
