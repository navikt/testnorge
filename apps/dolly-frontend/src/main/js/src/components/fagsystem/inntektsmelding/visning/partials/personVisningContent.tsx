import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import Button from '@/components/ui/button/Button'
import useBoolean from '@/utils/hooks/useBoolean'
import { Journalpost } from '@/components/fagsystem/inntektsmelding/InntektsmeldingTypes'
import styled from 'styled-components'
import { useDokument } from '@/utils/hooks/useJoarkDokument'
import { Alert } from '@navikt/ds-react'
import { lazy, Suspense } from 'react'
import Loading from '@/components/ui/loading/Loading'

interface PersonVisningContentProps {
	id: Journalpost
	idx: number
}

const StyledJournalpost = styled.div`
	background-color: #f7f7f7;
	position: relative;
	padding: 10px;
	border-radius: 4px;
	width: 100%;

	&& {
		h4 {
			margin-top: 0px;
		}
	}

	&&& {
		button {
			position: absolute;
			right: 23px;
			top: 60px;
		}
	}
`
const PrettyCode = lazy(() => import('@/components/codeView/PrettyCode'))

export const PersonVisningContent = ({ miljoe, dokumentInfo }: PersonVisningContentProps) => {
	const [viserSkjemainnhold, vis, skjul] = useBoolean(false)

	const { dokument, loading, error } = useDokument(
		dokumentInfo?.journalpostId,
		dokumentInfo?.dokumentInfoId,
		miljoe,
		'ORIGINAL',
	)

	return (
		<StyledJournalpost>
			<h4>Journalpost-ID</h4>
			<div className="person-visning_content">
				<TitleValue title="Journalpost-id" value={dokumentInfo.journalpostId} />
				<TitleValue title="Dokumentinfo-id" value={dokumentInfo.dokumentInfoId} />
				<Button
					onClick={viserSkjemainnhold ? skjul : vis}
					kind={viserSkjemainnhold ? 'chevron-up' : 'chevron-down'}
				>
					{(viserSkjemainnhold ? 'SKJUL ' : 'VIS ') + 'SKJEMAINNHOLD'}
				</Button>
			</div>
			{viserSkjemainnhold &&
				(dokument ? (
					<Suspense fallback={<Loading label={'Laster xml...'} />}>
						<PrettyCode language={'xml'} codeString={dokument} />
					</Suspense>
				) : (
					<Alert variant="error" size="small" inline>
						Kunne ikke hente skjemainnhold
					</Alert>
				))}
		</StyledJournalpost>
	)
}
