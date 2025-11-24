import * as React from 'react'
import { formatDate } from '@/utils/DataFormatter'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { SykemeldingSynt } from '@/components/fagsystem/sykdom/SykemeldingTypes'

export const SyntSykemelding = ({ sykemelding, idx }: SykemeldingSynt) => (
	<div key={idx} className="person-visning_content">
		<TitleValue title="Startdato" value={formatDate(sykemelding.startDato)} />
		<TitleValue title="Organisasjonsnummer" value={sykemelding.orgnummer} />
		<TitleValue title="Arbeidsforhold-ID" value={sykemelding.arbeidsforholdId} />
		<TitleValue
			title="Referanse for sporing av innsending"
			value={sykemelding.sykemeldingId}
		/>
	</div>
)
