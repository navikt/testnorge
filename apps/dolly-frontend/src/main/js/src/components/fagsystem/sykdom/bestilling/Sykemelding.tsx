import { isEmpty } from '@/components/fagsystem/pdlf/form/partials/utils'
import React from 'react'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import {
	BestillingData,
	BestillingTitle,
} from '@/components/bestillingsveileder/stegVelger/steg/steg3/Bestillingsvisning'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatDate } from '@/utils/DataFormatter'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import {
	SykemeldingAktivitet,
	SykemeldingBestilling,
	sykmeldingTypeLabels,
} from '@/components/fagsystem/sykdom/SykemeldingTypes'
import type { SykmeldingType } from '@/components/fagsystem/sykdom/SykemeldingTypes'

type SykemeldingProps = {
	sykemelding: SykemeldingBestilling
}

export const Sykemelding = ({ sykemelding }: SykemeldingProps) => {
	if (!sykemelding || isEmpty(sykemelding)) {
		return null
	}

	const nySykemelding = sykemelding.nySykemelding

	return (
		<div className="bestilling-visning">
			<ErrorBoundary>
				<BestillingTitle>Sykemelding</BestillingTitle>
				{nySykemelding && (
					<BestillingData>
						<TitleValue
							title="Type"
							value={
								nySykemelding.type
									? sykmeldingTypeLabels[nySykemelding.type as SykmeldingType] ||
										nySykemelding.type
									: undefined
							}
						/>
						<DollyFieldArray header="Periode" data={nySykemelding.aktivitet}>
							{(aktivitet: SykemeldingAktivitet, idx: number) => (
								<React.Fragment key={idx}>
									<TitleValue title="Grad (%)" value={aktivitet?.grad} />
									<TitleValue
										title="Reisetilskudd"
										value={aktivitet?.reisetilskudd ? 'Ja' : undefined}
									/>
									<TitleValue title="F.o.m. dato" value={formatDate(aktivitet?.fom)} />
									<TitleValue title="T.o.m. dato" value={formatDate(aktivitet?.tom)} />
								</React.Fragment>
							)}
						</DollyFieldArray>
					</BestillingData>
				)}
			</ErrorBoundary>
		</div>
	)
}
