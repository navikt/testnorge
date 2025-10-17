import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { getInitialGrunnlag } from '@/components/fagsystem/sigrunstubSummertSkattegrunnlag/form/Form'
import { KjoeretoeyArrayForm } from '@/components/fagsystem/sigrunstubSummertSkattegrunnlag/form/KjoeretoeyArrayForm'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'

export const tekniskNavnKodeverk = 'Summert skattegrunnlag'
export const kategoriKodeverk = 'Summert skattegrunnlag - kategori'

export const GrunnlagArrayForm = ({ path, header }) => (
	<FormDollyFieldArray
		name={path}
		header={header}
		newEntry={getInitialGrunnlag()}
		canBeEmpty={true}
		nested
	>
		{(grunnlagPath, idx) => (
			<div className="flexbox--flex-wrap sigrun-form" key={idx}>
				<FormSelect
					name={`${grunnlagPath}.tekniskNavn`}
					size={'xxxlarge'}
					label="Teknisk navn"
					kodeverk={tekniskNavnKodeverk}
					isClearable={false}
				/>
				<FormSelect
					name={`${grunnlagPath}.kategori`}
					size={'large'}
					label="Kategori"
					kodeverk={kategoriKodeverk}
					isClearable={false}
				/>
				<FormTextInput
					name={`${grunnlagPath}.andelOverfoertFraBarn`}
					label="Andel overført fra barn"
					type={'number'}
				/>
				<FormTextInput name={`${grunnlagPath}.beloep`} label="Beløp" type={'number'} />

				<KjoeretoeyArrayForm path={`${grunnlagPath}.spesifisering`} />
			</div>
		)}
	</FormDollyFieldArray>
)
