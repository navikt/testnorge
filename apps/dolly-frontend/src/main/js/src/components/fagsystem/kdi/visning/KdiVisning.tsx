import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'

export const sjekkManglerKdiData = (kdiData) => {
	return false
	// return kdiData?.length < 1 || kdiData?.every((miljoData) => miljoData.data?.length < 1)
}

export const KdiVisning = ({ data, loading, harKdiBestilling }) => {
	console.log('data: ', data) //TODO - SLETT MEG

	return (
		<ErrorBoundary>
			<SubOverskrift label="KDI-meldinger" iconKind="institusjon" />
		</ErrorBoundary>
	)
}
