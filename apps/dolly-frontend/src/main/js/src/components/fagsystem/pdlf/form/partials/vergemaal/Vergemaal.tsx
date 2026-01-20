import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { AvansertForm } from '@/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { initialVergemaal } from '@/components/fagsystem/pdlf/form/initialValues'
import { VergemaalKodeverk } from '@/config/kodeverk'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { PdlPersonExpander } from '@/components/fagsystem/pdlf/form/partials/pdlPerson/PdlPersonExpander'
import { isEmpty } from '@/components/fagsystem/pdlf/form/partials/utils'
import { Option } from '@/service/SelectOptionsOppslag'
import { UseFormReturn } from 'react-hook-form/dist/types'
import { initialTjenesteomraade, TjenesteomraadeForm } from './TjenesteomraadeForm'

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
			<FormSelect
				name={`${path}.vergemaalEmbete`}
				label="Fylkesembete"
				kodeverk={VergemaalKodeverk.Fylkesmannsembeter}
				size="xlarge"
			/>
			<FormSelect
				name={`${path}.sakType`}
				label="Sakstype"
				kodeverk={VergemaalKodeverk.Sakstype}
				size="xlarge"
				optionHeight={50}
			/>
			<FormSelect
				name={`${path}.mandatType`}
				label="Mandattype"
				kodeverk={VergemaalKodeverk.Mandattype}
				size="fullWidth"
				optionHeight={50}
			/>
			<FormDatepicker name={`${path}.gyldigFraOgMed`} label="Gyldig f.o.m." />
			<FormDatepicker name={`${path}.gyldigTilOgMed`} label="Gyldig t.o.m." />
			<FormDollyFieldArray
				name={`${path}.tjenesteomraade`}
				header="Tjenesteområde"
				newEntry={initialTjenesteomraade}
				canBeEmpty={true}
				style={{ marginBottom: '20px' }}
				nested
			>
				{(tjenesteomraadePath: string) => <TjenesteomraadeForm path={tjenesteomraadePath} />}
			</FormDollyFieldArray>
			<PdlPersonExpander
				path={path}
				nyPersonPath={`${path}.nyVergeIdent`}
				eksisterendePersonPath={`${path}.vergeIdent`}
				eksisterendeNyPerson={eksisterendeNyPerson}
				label={'VERGE'}
				formMethods={formMethods}
				isExpanded={
					!isEmpty(formMethods.watch(`${path}.nyVergeIdent`), ['syntetisk']) ||
					formMethods.watch(`${path}.vergeIdent`) !== null
				}
			/>
			<AvansertForm path={path} kanVelgeMaster={false} />
		</>
	)
}

export const Vergemaal = ({ formMethods }: VergemaalFormTypes) => {
	return (
		<div className="flexbox--flex-wrap">
			<FormDollyFieldArray
				name={'pdldata.person.vergemaal'}
				header="Vergemål"
				newEntry={initialVergemaal}
				canBeEmpty={false}
			>
				{(path: string, _idx: number) => <VergemaalForm formMethods={formMethods} path={path} />}
			</FormDollyFieldArray>
		</div>
	)
}
