import React from 'react'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'
import { ArbeidKodeverk } from '~/config/kodeverk'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'

export const PermisjonPermitteringer = ({ data }) => {
	if (!data || data.length === 0) return null

	const permisjon = []
	const permittering = []

	data.forEach((periode) => {
		if (periode.type === 'permittering') {
			permittering.push(periode)
		} else {
			permisjon.push(periode)
		}
	})

	return (
		<>
			{permisjon.length > 0 && (
				<>
					<h4>Permisjon</h4>
					<ErrorBoundary>
						<DollyFieldArray data={permisjon} nested>
							{(id, idx) => (
								<div className="person-visning_content" key={idx}>
									<TitleValue
										title="Permisjonstype"
										value={id.type}
										kodeverk={ArbeidKodeverk.PermisjonsOgPermitteringsBeskrivelse}
									/>
									{id.periode && (
										<TitleValue
											title="Startdato"
											value={Formatters.formatStringDates(id.periode.fom)}
										/>
									)}
									{id.periode && (
										<TitleValue
											title="Sluttdato"
											value={Formatters.formatStringDates(id.periode.tom)}
										/>
									)}
									<TitleValue title="Permisjonsprosent" value={id.prosent} />
								</div>
							)}
						</DollyFieldArray>
					</ErrorBoundary>
				</>
			)}
			{permittering.length > 0 && (
				<>
					<h4>Permittering</h4>
					<ErrorBoundary>
						<DollyFieldArray data={permittering} nested>
							{(id, idx) => (
								<div className="person-visning_content" key={idx}>
									{id.periode && (
										<TitleValue
											title="Startdato"
											value={Formatters.formatStringDates(id.periode.fom)}
										/>
									)}
									{id.periode && (
										<TitleValue
											title="Sluttdato"
											value={Formatters.formatStringDates(id.periode.tom)}
										/>
									)}
									<TitleValue title="Permitteringsprosent" value={id.prosent} />
								</div>
							)}
						</DollyFieldArray>
					</ErrorBoundary>
				</>
			)}
		</>
	)
}
