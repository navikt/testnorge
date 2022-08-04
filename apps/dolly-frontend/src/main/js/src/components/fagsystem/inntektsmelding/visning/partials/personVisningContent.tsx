import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Button from '~/components/ui/button/Button'
import { CodeView } from '~/components/codeView'
import React from 'react'
import useBoolean from '~/utils/hooks/useBoolean'
import { Journalpost } from '~/components/fagsystem/inntektsmelding/InntektsmeldingTypes'

interface PersonVisningContentProps {
	id: Journalpost
	idx: number
}

export const PersonVisningContent = ({ id, idx }: PersonVisningContentProps) => {
	const [viserSkjemainnhold, vis, skjul] = useBoolean(false)

	return (
		<div key={idx} className="person-visning_content">
			<TitleValue title="Miljø" value={id.miljoe.toUpperCase()} />
			<TitleValue title="Journalpost-id" value={id.dokumenter[0].journalpostId} />
			<TitleValue title="Dokumentinfo-id" value={id.dokumenter[0].dokumentInfoId} />

			<Button onClick={viserSkjemainnhold ? skjul : vis} kind="">
				{(viserSkjemainnhold ? 'SKJUL ' : 'VIS ') + 'SKJEMAINNHOLD'}
			</Button>
			{viserSkjemainnhold && <CodeView language="xml" code={id.dokumenter[0].dokument} />}
		</div>
	)
}
