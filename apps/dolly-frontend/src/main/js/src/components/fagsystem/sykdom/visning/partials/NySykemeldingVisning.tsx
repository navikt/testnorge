import * as React from 'react'
import { formatDate } from '@/utils/DataFormatter'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { useTsmSykemelding } from '@/utils/hooks/useSykemelding'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import Loading from '@/components/ui/loading/Loading'

export const NySykemeldingVisning = ({ ident, sykemeldinger: initialSykemeldinger }: any) => {
	const { sykemeldinger, error, loading } = initialSykemeldinger
		? { sykemeldinger: initialSykemeldinger, error: null, loading: false }
		: useTsmSykemelding(ident?.ident)

	const renderAktivitet = (sykemelding: any, idx: number) => (
		<DollyFieldArray key={idx} data={sykemelding.aktivitet} nested>
			{(aktivitet: any, idx: number) => (
				<div key={idx} className="person-visning_content">
					<TitleValue title="F.o.m. dato" value={formatDate(aktivitet.fom)} />
					<TitleValue title="T.o.m. dato" value={formatDate(aktivitet.tom)} />
				</div>
			)}
		</DollyFieldArray>
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
			<h3>Perioder</h3>
			<div className="person-visning_content">
				<div className="person-visning_content">
					{sykemeldinger?.[0] &&
						sykemeldinger?.[0]?.aktivitet?.length > 0 &&
						renderAktivitet(sykemeldinger?.[0], 0)}
				</div>
			</div>
		</React.Fragment>
	)
}
