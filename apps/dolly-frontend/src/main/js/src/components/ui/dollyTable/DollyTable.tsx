import Table from './table/Table'
import { DollyPagination } from '@/components/ui/dollyTable/pagination/DollyPagination'
import { useReduxSelector } from '@/utils/hooks/useRedux'

export const DollyTable = ({ data, pagination, gruppeDetaljer = {}, ...props }) => {
	const sidetall = useReduxSelector((state) => state?.finnPerson?.sidetall) || 0
	if (!pagination) return <Table data={data} {...props} />

	return (
		<DollyPagination
			visSide={sidetall}
			items={data}
			gruppeDetaljer={gruppeDetaljer}
			render={(items) => <Table data={items} {...props} />}
		/>
	)
}
