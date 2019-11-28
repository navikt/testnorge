import React from 'react'
import _get from 'lodash/get'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'
import Loading from '~/components/ui/loading/Loading'

export const SigrunstubVisning = ({ data, loading, visTittel = true }) => {
	if (loading) return <Loading label="laster sigrunstub-data" />
	if (!data) return false

	const grunnlag = data[0].grunnlag.length > 0
	const svalbardGrunnlag = data[0].svalbardGrunnlag.length > 0

	return (
		<div>
			{visTittel && <SubOverskrift label="Inntekt" />}
			<div className="person-visning_content">
				{grunnlag && (
					<div>
						<h4>Fastlands-Norge</h4>
						{data[0].grunnlag.map((inntekt, idx) => (
							<div key={idx} className="flexbox">
								<TitleValue title="" value={`#${idx + 1}`} size="x-small" />
								<TitleValue title="Inntektsår" value={inntekt.inntektsaar} />
								<TitleValue title="Tjeneste" value={inntekt.tjeneste} />
								<TitleValue
									title="Type inntekt"
									value={inntekt.grunnlag}
									kodeverk={inntekt.tjeneste}
								/>
								<TitleValue title="Beløp" value={inntekt.verdi} />
							</div>
						))}
					</div>
				)}
				{svalbardGrunnlag && (
					<div>
						<h4>Svalbard</h4>
						{data[0].svalbardGrunnlag.map((inntekt, idx) => (
							<div key={idx} className="flexbox">
								<TitleValue title="" value={`#${idx + 1}`} size="x-small" />
								<TitleValue title="Inntektsår" value={inntekt.inntektsaar} />
								<TitleValue title="Tjeneste" value={inntekt.tjeneste} />
								<TitleValue
									title="Type inntekt"
									value={inntekt.grunnlag}
									kodeverk={inntekt.tjeneste}
								/>
								<TitleValue title="Beløp" value={inntekt.verdi} />
							</div>
						))}
					</div>
				)}
			</div>
		</div>
	)
}
