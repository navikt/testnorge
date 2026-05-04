import * as React from 'react'
import { formatDate } from '@/utils/DataFormatter'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { useTsmSykemelding } from '@/utils/hooks/useSykemelding'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import Loading from '@/components/ui/loading/Loading'
import { sykmeldingTypeLabels } from '@/components/fagsystem/sykdom/SykemeldingTypes'
import type { SykmeldingType } from '@/components/fagsystem/sykdom/SykemeldingTypes'

export const SykemeldingVisning = ({ ident, sykemeldinger: initialSykemeldinger }: any) => {
	const fetched = useTsmSykemelding(ident?.ident)
	const { sykemeldinger, error, loading } = initialSykemeldinger
		? { sykemeldinger: initialSykemeldinger, error: null, loading: false }
		: fetched

	const renderAktivitet = (sykemelding: any, idx: number) => (
		<div key={idx} style={{ marginBottom: '1rem', width: '100%' }}>
			<div className="person-visning_content">
				<TitleValue
					title="Type"
					value={
						sykemelding.type
							? sykmeldingTypeLabels[sykemelding.type as SykmeldingType] || sykemelding.type
							: undefined
					}
				/>
				{sykemelding.sykmeldingId && (
					<TitleValue title="Sykmelding-ID" value={sykemelding.sykmeldingId} />
				)}
			</div>
			<DollyFieldArray data={sykemelding.aktivitet} nested>
				{(aktivitet: any, aIdx: number) => (
					<div key={aIdx} className="person-visning_content">
						<TitleValue title="Grad (%)" value={aktivitet.grad} />
						<TitleValue
							title="Reisetilskudd"
							value={aktivitet.reisetilskudd ? 'Ja' : undefined}
						/>
						<TitleValue title="F.o.m. dato" value={formatDate(aktivitet.fom)} />
						<TitleValue title="T.o.m. dato" value={formatDate(aktivitet.tom)} />
					</div>
				)}
			</DollyFieldArray>
		</div>
	)

	if (loading) {
		return <Loading label={'Henter sykemeldinger...'} />
	}
	if (error) {
		return <div>Feil ved henting av sykemeldinger</div>
	}
	if (!loading && !error && sykemeldinger?.length > 0 && !sykemeldinger[0]?.aktivitet?.length) {
		return <div>Fant sykemelding uten aktivitetsperioder</div>
	}

	return (
		<React.Fragment>
			<h3>Sykemeldinger</h3>
			<div className="person-visning_content">
				{sykemeldinger?.map((sykemelding: any, idx: number) =>
					sykemelding?.aktivitet?.length > 0 ? renderAktivitet(sykemelding, idx) : null,
				)}
			</div>
		</React.Fragment>
	)
}
