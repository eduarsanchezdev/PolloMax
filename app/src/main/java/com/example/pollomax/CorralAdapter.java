package com.example.pollomax;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class CorralAdapter extends RecyclerView.Adapter<CorralAdapter.ViewHolder> {
    private List<Corral> corrales;
    private Context context;

    public CorralAdapter(Context context, List<Corral> corrales) {
        this.context = context;
        this.corrales = corrales;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_corral, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Corral corral = corrales.get(position);

        holder.tvNombre.setText(corral.getNombre());
        holder.tvCantidad.setText(String.valueOf(corral.getCantidadPollos()));
        holder.tvPrecioPollos.setText(
                String.format(Locale.getDefault(), "%.2f", corral.getPrecioPollos())
        );

        // Mostrar fecha de creación
        holder.tvFechaCreacion.setText(corral.getFechaCreacion());

        holder.itemCardView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetalleCorralActivity.class);
            intent.putExtra("CORRAL_ID", corral.getId());
            intent.putExtra("NOMBRE_CORRAL", corral.getNombre());
            intent.putExtra("CANTIDAD_POLLOS", String.valueOf(corral.getCantidadPollos()));
            intent.putExtra("PRECIO_POLLOS", corral.getPrecioPollos());
            intent.putExtra("FECHA_CREACION", corral.getFechaCreacion());
            
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return corrales != null ? corrales.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvCantidad, tvPrecioPollos, tvFechaCreacion;
        CardView itemCardView;

        public ViewHolder(View itemView) {
            super(itemView);
            itemCardView = itemView.findViewById(R.id.cardDetallesCorral);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvCantidad = itemView.findViewById(R.id.tvCantidad);
            tvPrecioPollos = itemView.findViewById(R.id.tvPrecioPollos);
            tvFechaCreacion = itemView.findViewById(R.id.tvFechaCreacion);
        }
    }
}
