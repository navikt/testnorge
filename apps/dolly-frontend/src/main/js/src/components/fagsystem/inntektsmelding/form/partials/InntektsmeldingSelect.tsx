import LoadableComponent, { Feilmelding } from '@/components/ui/loading/LoadableComponent'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { Option, SelectOptionsOppslag } from '@/service/SelectOptionsOppslag'
import { codeToNorskLabel } from '@/utils/DataFormatter'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'

interface InntektsmeldingSelect {
	path: string
	label: string
	kodeverk: string
	size?: string
}

export default ({ path, label, kodeverk, size = 'medium' }: InntektsmeldingSelect) => {
	return (
		<ErrorBoundary>
			<LoadableComponent
				onFetch={() =>
					SelectOptionsOppslag.hentInntektsmeldingOptions(kodeverk).then((response) =>
						response.map((value: string) => ({ value, label: codeToNorskLabel(value) })),
					)
				}
				render={(data: Array<Option>, feilmelding: Feilmelding) => (
					<FormSelect
						name={path}
						label={label}
						options={data}
						type="text"
						size={size}
						feil={feilmelding}
					/>
				)}
			/>
		</ErrorBoundary>
	)
}
