import { FullmaktType } from '@/components/fagsystem/fullmakt/FullmaktType'
import { useFullmektig } from '@/utils/hooks/useFullmakt'
import React, { useEffect, useState } from 'react'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { Fullmakt } from '@/components/fagsystem/fullmakt/visning/Fullmakt'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'

type FullmaktProps = {
	ident: string
}

export const FullmaktVisning = ({ ident }: FullmaktProps) => {
	const { fullmektig } = useFullmektig(ident)
	const [fullmaktData, setFullmaktData] = useState([])

	useEffect(() => {
		if (fullmektig?.length > 0) {
			setFullmaktData(fullmektig)
		}
	}, [fullmektig])

	if (!fullmektig || fullmektig.length === 0) {
		return null
	}

	return (
		<ErrorBoundary>
			<div>
				<SubOverskrift label="Fullmakt" iconKind="fullmakt" />
				<div className="person-visning_content">
					<DollyFieldArray
						data={fullmaktData}
						getHeader={(fullmakt: FullmaktType) =>
							`Fullmektig: ${fullmakt?.fullmektigsNavn} (${fullmakt?.fullmektig})`
						}
					>
						{(item: FullmaktType, idx: number) => <Fullmakt fullmakt={item} idx={idx} />}
					</DollyFieldArray>
				</div>
			</div>
		</ErrorBoundary>
	)
}

export default FullmaktVisning
